# hyolib
Hyowon General library &amp; Hyowon Cinema Editor
* This program requires PW Library. check it on our repository. (latest version)

# what is it?
It is a implementation of 3D graphics from scratch used Java.
Currently, For General library, 3D rotation matrix, perspective, texture(UV) mapping, normal mapping, specular, diffuse, t/r/s, parenting, animation, keyframe, skeleton, joint, particle, obj model and mtl library loading, coloring, clipping, triangle drawing, physics integrator, per-object handling, transform, vector calculating, serializing is supported.

# what is hyowon cinema editor ?
It is a 3D Flim maker using hyowon general library(hyolib) and made with Java.
Currently, scene, serializing, t/r/s keyframe-motioning, sound, subtitle, gui-editing, active-motioning, meta-motioning, particle-motioning, texture inserting, object lerping, scene editing, playing-stoping, copying, pasting, dragging, indicating, etc is supported.

# examples of cinema
you can see examples in folder 'hyonema/scenes'. there are 3 different scenes, you can play them by loading their file.

# creating cinema
hyowon cinema editor is very easy to use. but texture file size or model vertices length might be small, for texture file size, under 2000x2000. for model vertices length, under 10,000. (Total) so you must be use right action, espcially 'active motion'. so good luck!
