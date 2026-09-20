package com.example;

import org.joml.Vector2f;
import org.joml.Vector3f;
import valthorne.graphics.Color;
import valthorne.graphics.model.Material3D;
import valthorne.graphics.model.Model3D;
import valthorne.graphics.model.ModelInstance3D;
import valthorne.graphics.model.Scene3D;
import valthorne.graphics.texture.Texture;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;

/** Incrementally decodes the packed tree without blocking browser startup. */
final class PackedTree implements AutoCloseable {
    private static final int PARTS = 3;
    private static final int VERTICES_PER_FRAME = 4096;
    private static final int TRIANGLES_PER_CHUNK = 4096;
    private static final String[] TEXTURES = {"branches.jpg", "trunk.jpg", "leaves.png"};

    private final ArrayList<ModelInstance3D> parts = new ArrayList<>();
    private final Texture[] textures = new Texture[PARTS];
    private DataInputStream input;
    private Vector3f[] positions, normals;
    private Vector2f[] uvs;
    private Model3D.Triangle[] chunk;
    private Material3D material;
    private int part, vertexCount, faceCount, vertexIndex, faceIndex, chunkSize;
    private boolean complete, closed;
    float width, height;

    PackedTree() {
        try {
            var resource = PackedTree.class.getResourceAsStream("/models/jacaranda/tree.vtr.gz");
            if (resource == null) throw new IOException("Missing packed tree");
            input = new DataInputStream(new GZIPInputStream(resource));
            if (input.readInt() != 0x56545231 || input.readInt() != PARTS)
                throw new IOException("Invalid tree header");
            beginPart();
        } catch (IOException | RuntimeException error) {
            close();
            throw new IllegalStateException("Unable to open tree", error);
        }
    }

    /** Advances bounded decoding work and produces at most one mesh chunk. */
    boolean advance(Scene3D scene) {
        if (complete) return true;
        try {
            int decodedVertices = 0;
            while (vertexIndex < vertexCount && decodedVertices++ < VERTICES_PER_FRAME) {
                var position = positions[vertexIndex] = new Vector3f(input.readFloat(), input.readFloat(), input.readFloat());
                normals[vertexIndex] = new Vector3f(input.readFloat(), input.readFloat(), input.readFloat());
                uvs[vertexIndex] = new Vector2f(input.readFloat(), input.readFloat());
                width = Math.max(width, Math.abs(position.x) * 2);
                height = Math.max(height, position.z);
                vertexIndex++;
            }
            if (vertexIndex < vertexCount) return false;

            if (material == null) loadMaterial();
            int decodedFaces = 0;
            while (faceIndex < faceCount && decodedFaces++ < TRIANGLES_PER_CHUNK) {
                int a = input.readInt(), b = input.readInt(), c = input.readInt();
                if (a < 0 || b < 0 || c < 0 || a >= vertexCount || b >= vertexCount || c >= vertexCount)
                    throw new IOException("Invalid tree index");
                chunk[chunkSize++] = new Model3D.Triangle(positions[a], positions[b], positions[c], Color.WHITE,
                        uvs[a], uvs[b], uvs[c], normals[a], normals[b], normals[c]);
                faceIndex++;
            }
            if (chunkSize == TRIANGLES_PER_CHUNK || faceIndex == faceCount) {
                flushChunk(scene);
                if (faceIndex == faceCount) finishPart();
            }
            return complete;
        } catch (IOException | RuntimeException error) {
            close();
            throw new IllegalStateException("Unable to load tree", error);
        }
    }

    Iterable<ModelInstance3D> parts() {return parts;}
    boolean isComplete() {return complete;}

    private void beginPart() throws IOException {
        vertexCount = input.readInt();
        faceCount = input.readInt();
        if (vertexCount < 1 || vertexCount > 250000 || faceCount < 1 || faceCount > 100000)
            throw new IOException("Invalid tree counts");
        positions = new Vector3f[vertexCount];
        normals = new Vector3f[vertexCount];
        uvs = new Vector2f[vertexCount];
        chunk = new Model3D.Triangle[Math.min(TRIANGLES_PER_CHUNK, faceCount)];
        vertexIndex = faceIndex = chunkSize = 0;
        material = null;
    }

    private void loadMaterial() throws IOException {
        try (var image = PackedTree.class.getResourceAsStream("/models/jacaranda/" + TEXTURES[part])) {
            if (image == null) throw new IOException("Missing tree texture: " + TEXTURES[part]);
            textures[part] = new Texture(image.readAllBytes());
        }
        material = new Material3D().setTexture(textures[part]).setRoughness(.85f)
                .setAlphaCutoff(.45f).setCullBackFaces(false);
    }

    private void flushChunk(Scene3D scene) {
        if (chunkSize == 0) return;
        var triangles = chunkSize == chunk.length ? chunk : Arrays.copyOf(chunk, chunkSize);
        var instance = new ModelInstance3D().setModel(new Model3D(triangles)).setMaterial(material);
        parts.add(instance);
        scene.add(instance);
        int remaining = faceCount - faceIndex;
        chunk = remaining == 0 ? null : new Model3D.Triangle[Math.min(TRIANGLES_PER_CHUNK, remaining)];
        chunkSize = 0;
    }

    private void finishPart() throws IOException {
        positions = normals = null;
        uvs = null;
        chunk = null;
        material = null;
        part++;
        if (part < PARTS) {
            beginPart();
            return;
        }
        if (input.read() != -1) throw new IOException("Trailing tree data");
        input.close();
        input = null;
        complete = true;
    }

    @Override
    public void close() {
        if (closed) return;
        closed = true;
        if (input != null) try {input.close();} catch (IOException ignored) {}
        input = null;
        for (var texture : textures) if (texture != null) texture.dispose();
    }
}
