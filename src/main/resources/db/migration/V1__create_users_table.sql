CREATE TABLE users (
                       id                  UUID PRIMARY KEY,
                       email               VARCHAR(255) NOT NULL UNIQUE,
                       password_hash       VARCHAR(255) NOT NULL,
                       status              VARCHAR(30)  NOT NULL,
                       email_verified      BOOLEAN      NOT NULL DEFAULT FALSE,
                       failed_login_count  INT          NOT NULL DEFAULT 0,
                       last_login_at       TIMESTAMPTZ,
                       created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
                       updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now()
);