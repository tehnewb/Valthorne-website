// Offline asset conversion only. The website itself remains a Java application.
// npm install --prefix build/model-tools --no-save meshoptimizer sharp
import fs from 'node:fs';
import path from 'node:path';
import {MeshoptSimplifier} from '../build/model-tools/node_modules/meshoptimizer/index.js';
import {createRequire} from 'node:module';
const sharp=createRequire(new URL('../build/model-tools/package.json',import.meta.url))('sharp');
const root=path.resolve(import.meta.dirname,'..');
const source=path.join(root,'build/model-sources/jacaranda');
const output=path.join(root,'src/main/resources/models/jacaranda');
const gltf=JSON.parse(fs.readFileSync(path.join(source,'tree.gltf'),'utf8'));
const buffer=fs.readFileSync(path.join(source,'jacaranda_tree.bin'));
if(gltf.nodes.length!==1||gltf.nodes[0].matrix||gltf.nodes[0].translation||gltf.nodes[0].rotation||gltf.nodes[0].scale)
    throw new Error('Expected the verified identity-transform Jacaranda source');
function accessor(index){
    const a=gltf.accessors[index],view=gltf.bufferViews[a.bufferView];
    const size={SCALAR:1,VEC2:2,VEC3:3}[a.type];
    if(!size||![5125,5126].includes(a.componentType)||a.sparse)throw new Error('Unsupported accessor');
    const out=a.componentType===5126?new Float32Array(a.count*size):new Uint32Array(a.count*size);
    const stride=view.byteStride||size*4,offset=(view.byteOffset||0)+(a.byteOffset||0);
    for(let i=0;i<a.count;i++)for(let j=0;j<size;j++)out[i*size+j]=a.componentType===5126?
        buffer.readFloatLE(offset+i*stride+j*4):buffer.readUInt32LE(offset+i*stride+j*4);
    return out;
}
await MeshoptSimplifier.ready;
const obj=['# Poly Haven Jacaranda Tree; CC0. Simplified for the landing-page exhibit.','mtllib tree.mtl'];
const parts=['branches','trunk','leaves'],report=[];
let base=1;
const number=value=>String(Math.round(value*1000000)/1000000);
for(const [part,p] of gltf.meshes[0].primitives.entries()){
    const pos=accessor(p.attributes.POSITION),normal=accessor(p.attributes.NORMAL),uv=accessor(p.attributes.TEXCOORD_0);
    const indices=accessor(p.indices);
    const attributes=new Float32Array(pos.length/3*5);
    for(let i=0;i<pos.length/3;i++)attributes.set([normal[i*3],normal[i*3+1],normal[i*3+2],uv[i*2],uv[i*2+1]],i*5);
    const [reduced,error]=MeshoptSimplifier.simplifyWithAttributes(indices,pos,3,attributes,5,[.1,.1,.1,.1,.1],null,[24000,12000,64000][part]*3,.04,part===1?['Prune','Permissive']:['Prune']);
    const used=new Map();for(const index of reduced)if(!used.has(index))used.set(index,used.size+base);
    obj.push(`o ${parts[part]}`,`usemtl ${parts[part]}`);
    for(const index of used.keys())obj.push(`v ${number(pos[index*3])} ${number(pos[index*3+1])} ${number(pos[index*3+2])}`);
    for(const index of used.keys())obj.push(`vt ${number(uv[index*2])} ${number(1-uv[index*2+1])}`);
    for(const index of used.keys())obj.push(`vn ${number(normal[index*3])} ${number(normal[index*3+1])} ${number(normal[index*3+2])}`);
    for(let i=0;i<reduced.length;i+=3)obj.push('f '+[0,1,2].map(j=>{const n=used.get(reduced[i+j]);return `${n}/${n}/${n}`;}).join(' '));
    base+=used.size;report.push({part:parts[part],sourceTriangles:indices.length/3,triangles:reduced.length/3,vertices:used.size,error});
}
fs.writeFileSync(path.join(source,'tree.obj'),obj.join('\n')+'\n');
fs.writeFileSync(path.join(source,'tree.mtl'),parts.map(p=>`newmtl ${p}\nKd 1 1 1\nmap_Kd ${p==='leaves'?'leaves.png':p+'.jpg'}\n`).join('\n'));
// Pack the supplied opacity mask into the diffuse texture's alpha channel.
const alpha=await sharp(path.join(source,'leaves_alpha.png')).extractChannel(0).toBuffer();
await sharp(path.join(source,'leaves.jpg')).removeAlpha().joinChannel(alpha).png().toFile(path.join(output,'leaves.png'));
fs.writeFileSync(path.join(source,'conversion.json'),JSON.stringify(report,null,2)+'\n');
console.log(JSON.stringify(report,null,2));
