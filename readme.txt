# RC-1920

Computer Networks course project.

## Build

```
make
```

## Run

Server:

```
./FS/Fs [-p port]
```

Client:

```
./user/user [-i ip] [-p port]
```

## Server Files Structure

```
TOPICS/
      /topic/
      /topic/user                               <-- user id
      /topic/question
            /question/user                      <-- user id
            /question/data                      <-- question content
            /question/img_ext                   <-- image extension, if exists
            /question/img                       <-- image data, if exists
            /question/<answer_number>
                     /<answer_number>/user      <-- user id
                     /<answer_number>/data      <-- anwser content
                     /<answer_number>/img_ext   <-- image extension, if exists
                     /<answer_number>/img       <-- image data, if exists
```