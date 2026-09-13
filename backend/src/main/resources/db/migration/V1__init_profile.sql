-- CareerDeck 初期スキーマ
-- 縦の貫通確認用の最小テーブル。機能追加は V2 以降のマイグレーションで行う。

create table profile (
    id           uuid         primary key,
    display_name varchar(100) not null,
    headline     varchar(200) not null default '',
    created_at   timestamptz  not null default now(),
    updated_at   timestamptz  not null default now()
);

create unique index idx_profile_display_name on profile (lower(display_name));

comment on table profile is 'ユーザーのプロフィール（スキル・経歴のルート）';
