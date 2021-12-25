# PocketZotero
![image info](./README-files/cover.svg)

The project aims for providing a simple Android application to read the offline local Zotero databases. The idea is to ignore Zotero Cloud completely and only use the offline copy of the database which could be obtained from the desktop version of Zotero.

## APK
I usually release unsigned working APKs of the applications. You can find them at [Releases](https://github.com/salehjg/PocketZotero/releases).

## Features
- [x] Single Library
- [x] Read-only Mode
- [x] Accessing the default DB located at `/storage/emulated/0/PocketZotero/zotero.sqlite`  
- [x] Offline and local Zotero DB access 
- [x] Decoding the nested collection structure
- [x] Decoding the fields and their values
- [x] Decoding the authors and their indices
- [x] Decoding the attachments and their keys and paths
- [ ] Accessing the database over Windows SMB
- [x] Opening the attachments stored locally
- [ ] Opening the attachments stored on the local network (SMB)
- [x] Decoding the notes for each CollectionItem
- [x] Decoding the tags for each 
- [ ] Reading databases with multiple libraries
- [ ] Global item search
- [ ] Global tag search


## Credits
Here is the list of the open-source projects and/or free sources that are used in this project.

[ZotDroid](https://github.com/ARF1/ZotDroid)

[TreeView](https://github.com/mayankneeds/TreeView-Android)

[SVGrepo](https://www.svgrepo.com/)
