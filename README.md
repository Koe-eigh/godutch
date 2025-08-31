# Go Dutch
Go Dutchは、グループでの支払いを簡単に管理するためのアプリケーションです。

## ローカル開発環境のセットアップ

### 前提条件
- Docker & Docker Compose
- Node.js 18.18.0以上 (フロントエンド開発時)
- Java 22 & Maven (API開発時)

### 手順

1. **環境変数ファイルのセットアップ**
   ```bash
   make setup
   # または
   cp .env.example .env
   ```

2. **Docker Composeでの起動**
   ```bash
   # 全サービス (MySQL + API + Web) を起動
   docker-compose up -d
   
   # ログを確認
   docker-compose logs -f
   ```

3. **フロントエンドの開発サーバー起動** (オプション)
   ```bash
   cd web
   npm install
   npm run dev
   ```
   ※ この場合は http://localhost:5173 でアクセス

### アクセス
- **Web アプリ**: http://localhost:3000 (Docker) または http://localhost:5173 (開発サーバー)
- **API**: http://localhost:8080
- **API ヘルスチェック**: http://localhost:8080/actuator/health

### 停止
```bash
docker-compose down
```

### API のみ開発する場合
```bash
# MySQL のみ起動
docker-compose up -d mysql

# API を IDE または maven で起動
cd api
mvn spring-boot:run
```