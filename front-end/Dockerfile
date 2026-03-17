FROM node:latest as build
RUN npm install -g @angular/cli

RUN mkdir /front
WORKDIR /front

COPY *.json /front/
RUN npm install

COPY . .
#RUN node ./mock/server.js

CMD ["ng", "serve"]