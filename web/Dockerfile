FROM node:22-alpine AS build

WORKDIR /app

COPY package*.json ./

RUN npm ci

COPY . .

RUN npm run build

FROM nginx:alpine AS production

ENV NODE_ENV=production \
    API_URL=/api

# Nginx のドキュメントルート
WORKDIR /usr/share/nginx/html

# ビルド成果物配置
COPY --from=build /app/build ./

# Nginx 設定テンプレートを配置（公式エントリポイントが起動時に envsubst で /etc/nginx/conf.d に出力）
COPY ./nginx/default.conf.template /etc/nginx/templates/default.conf.template