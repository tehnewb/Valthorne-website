package com.example;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import org.joml.Vector2f;
import org.joml.Vector3f;
import valthorne.graphics.Color;
import valthorne.graphics.model.*;
import valthorne.graphics.texture.Texture;

/** Preconverted binary geometry avoids runtime OBJ tokenization and axis conversion. */
final class PackedTree implements AutoCloseable {
    final ModelInstance3D[] parts = new ModelInstance3D[3];
    private final Texture[] textures = new Texture[3];
    float width, height;
    PackedTree(Scene3D scene) {
        try (var resource = PackedTree.class.getResourceAsStream("/models/jacaranda/tree.vtr.gz")) {
            if (resource == null) throw new IOException("Missing packed tree");
            try(var in = new DataInputStream(new GZIPInputStream(resource))) {
                if (in.readInt()!=0x56545231 || in.readInt()!=3) throw new IOException("Invalid tree header");
                String[] names={"branches.jpg","trunk.jpg","leaves.png"};
                for(int part=0;part<3;part++) {
                    int count=in.readInt(), faces=in.readInt();
                    if(count<1||count>250000||faces<1||faces>100000) throw new IOException("Invalid tree counts");
                    Vector3f[] p=new Vector3f[count], n=new Vector3f[count]; Vector2f[] uv=new Vector2f[count];
                    for(int i=0;i<count;i++) {
                        p[i]=new Vector3f(in.readFloat(),in.readFloat(),in.readFloat());
                        n[i]=new Vector3f(in.readFloat(),in.readFloat(),in.readFloat()); uv[i]=new Vector2f(in.readFloat(),in.readFloat());
                        width=Math.max(width,Math.abs(p[i].x)*2);
                        height=Math.max(height,p[i].z);
                    }
                    Model3D.Triangle[] triangles=new Model3D.Triangle[faces];
                    for(int i=0;i<faces;i++) {
                        int a=in.readInt(), b=in.readInt(), c=in.readInt();
                        if(a<0||b<0||c<0||a>=count||b>=count||c>=count) throw new IOException("Invalid tree index");
                        triangles[i]=new Model3D.Triangle(p[a],p[b],p[c],Color.WHITE,uv[a],uv[b],uv[c],n[a],n[b],n[c]);
                    }
                    try(var image=PackedTree.class.getResourceAsStream("/models/jacaranda/"+names[part])) {
                        if(image==null)throw new IOException("Missing tree texture: "+names[part]);
                        textures[part]=new Texture(image.readAllBytes());
                    }
                    parts[part]=new ModelInstance3D().setModel(new Model3D(triangles)).setMaterial(new Material3D().setTexture(textures[part]).setRoughness(.85f).setAlphaCutoff(.45f).setCullBackFaces(false));
                }
                if(in.read()!=-1) throw new IOException("Trailing tree data");
            }
            for(var part:parts) scene.add(part);
        } catch(IOException|RuntimeException error) { close(); throw new IllegalStateException("Unable to load tree",error); }
    }
    @Override public void close(){for(var texture:textures)if(texture!=null)texture.dispose();}
}
