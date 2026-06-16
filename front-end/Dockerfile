# Usar uma versão específica com a tag 'alpine' deixa a imagem muito menor e evita quebras futuras
FROM node:20-alpine

# Instala o Angular CLI globalmente
RUN npm install -g @angular/cli

# O WORKDIR já cria a pasta automaticamente, não sendo necessário o comando 'mkdir'
WORKDIR /front

# Copia os arquivos de dependência primeiro (package.json e package-lock.json)
COPY package*.json ./
RUN npm install

# Copia o restante do projeto
COPY . .

# Expõe a porta para documentar qual porta o container utiliza
EXPOSE 8080

# Inicia o servidor Angular na porta 8080 e libera o host para acesso externo
CMD ["ng", "serve", "--host", "0.0.0.0", "--port", "8080"]