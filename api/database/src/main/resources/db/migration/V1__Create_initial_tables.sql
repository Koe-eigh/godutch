-- 初期テーブル作成用のマイグレーション
-- Flyway naming convention: V{version}__{description}.sql

CREATE TABLE tbl_groups (
  id CHAR(36) PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  description VARCHAR(255)
);

CREATE TABLE tbl_members (
  id CHAR(36) PRIMARY KEY,
  group_id CHAR(36) NOT NULL,
  name VARCHAR(100) NOT NULL,
  UNIQUE (group_id, name),
  FOREIGN KEY (group_id) REFERENCES tbl_groups(id) ON DELETE CASCADE
);

CREATE TABLE tbl_payment_events (
  id CHAR(36) PRIMARY KEY,
  group_id CHAR(36) NOT NULL,
  title VARCHAR(255) NOT NULL,
  memo VARCHAR(255),
  event_date DATE NOT NULL,
  FOREIGN KEY (group_id) REFERENCES tbl_groups(id) ON DELETE CASCADE
);

CREATE TABLE tbl_payment_event_creditors (
  event_id CHAR(36),
  member_id CHAR(36),
  amount BIGINT NOT NULL,
  PRIMARY KEY (event_id, member_id),
  FOREIGN KEY (event_id) REFERENCES tbl_payment_events(id) ON DELETE CASCADE,
  FOREIGN KEY (member_id) REFERENCES tbl_members(id) ON DELETE CASCADE
);

CREATE TABLE tbl_payment_event_debtors (
  event_id CHAR(36),
  member_id CHAR(36),
  amount BIGINT NOT NULL,
  PRIMARY KEY (event_id, member_id),
  FOREIGN KEY (event_id) REFERENCES tbl_payment_events(id) ON DELETE CASCADE,
  FOREIGN KEY (member_id) REFERENCES tbl_members(id) ON DELETE CASCADE
);

CREATE TABLE tbl_settlements (
  group_id CHAR(36),
  payer_id CHAR(36),
  receiver_id CHAR(36),
  amount BIGINT NOT NULL,
  is_paid BOOLEAN DEFAULT FALSE,
  PRIMARY KEY (group_id, payer_id, receiver_id),
  FOREIGN KEY (group_id) REFERENCES tbl_groups(id) ON DELETE CASCADE,
  FOREIGN KEY (payer_id) REFERENCES tbl_members(id) ON DELETE CASCADE,
  FOREIGN KEY (receiver_id) REFERENCES tbl_members(id) ON DELETE CASCADE
);
