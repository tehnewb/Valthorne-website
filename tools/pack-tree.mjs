import fs from 'node:fs';
import path from 'node:path';
import zlib from 'node:zlib';
const root=path.resolve(import.meta.dirname,'..');
const source=path.join(root,'build/model-sources/jacaranda/tree.obj');
const positions=[],normals=[],uvs=[],groups=[];
let group;
for(const line of fs.readFileSync(source,'utf8').split('\n')) {
 const p=line.split(' ');
 if(p[0]==='v')positions.push(p.slice(1).map(Number));
 if(p[0]==='vn')normals.push(p.slice(1).map(Number));
 if(p[0]==='vt')uvs.push(p.slice(1).map(Number));
 if(p[0]==='usemtl'){group={name:p[1],faces:[]};groups.push(group);}
 if(p[0]==='f')group.faces.push(p.slice(1).map(v=>Number(v.split('/')[0])-1));
}
const min=[Infinity,Infinity,Infinity],max=[-Infinity,-Infinity,-Infinity];
for(const v of positions)for(let k=0;k<3;k++){min[k]=Math.min(min[k],v[k]);max[k]=Math.max(max[k],v[k]);}
const chunks=[];
function ints(...n){const b=Buffer.alloc(n.length*4);n.forEach((v,i)=>b.writeInt32BE(v,i*4));chunks.push(b);}
function floats(...n){const b=Buffer.alloc(n.length*4);n.forEach((v,i)=>b.writeFloatBE(v,i*4));chunks.push(b);}
ints(0x56545231,groups.length);
for(const g of groups){
 const used=new Map();for(const f of g.faces)for(const i of f)if(!used.has(i))used.set(i,used.size);
 ints(used.size,g.faces.length);
 for(const i of used.keys()){
  const v=positions[i],n=normals[i],uv=uvs[i];
  floats(v[0]-(min[0]+max[0])/2,v[2]-(min[2]+max[2])/2,v[1]-min[1],n[0],n[2],n[1],...uv);
 }
 for(const f of g.faces)ints(used.get(f[0]),used.get(f[2]),used.get(f[1]));
}
const raw=Buffer.concat(chunks),packed=zlib.gzipSync(raw,{level:9});
fs.writeFileSync(path.join(root,'src/main/resources/models/jacaranda/tree.vtr.gz'),packed);
console.log(JSON.stringify({triangles:groups.reduce((n,g)=>n+g.faces.length,0),objBytes:fs.statSync(source).size,binaryBytes:raw.length,compressedBytes:packed.length}));
