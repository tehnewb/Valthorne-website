import fs from 'node:fs';
import zlib from 'node:zlib';
import assert from 'node:assert/strict';
const data=zlib.gunzipSync(fs.readFileSync(new URL('../src/main/resources/models/jacaranda/tree.vtr.gz',import.meta.url)));
let offset=0,total=0;
const int=()=>{const n=data.readInt32BE(offset);offset+=4;return n;};
assert.equal(int(),0x56545231);assert.equal(int(),3);
for(let part=0;part<3;part++) {
 const vertices=int(),triangles=int();assert(vertices>0&&vertices<=250000);assert(triangles>0&&triangles<=100000);
 for(let i=0;i<vertices*8;i++){assert(Number.isFinite(data.readFloatBE(offset)));offset+=4;}
 for(let i=0;i<triangles*3;i++){const index=int();assert(index>=0&&index<vertices);}
 total+=triangles;
}
assert.equal(offset,data.length);assert.equal(total,98375);
console.log(`Packed tree validated: ${total} triangles, three materials, finite attributes and valid indices.`);
