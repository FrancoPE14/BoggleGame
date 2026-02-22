FROM node:25
WORKDIR /app
COPY package*.json ./
RUN npm install
RUN npm install bootstrap@5.3.8
COPY . .
EXPOSE 3000
CMD ["npm", "run", "dev"]