CREATE TABLE refresh_tokens (
                                id          UUID PRIMARY KEY,
                                user_id     UUID NOT NULL REFERENCES users(id),
                                token_hash  VARCHAR(255) NOT NULL UNIQUE,
                                family_id   UUID NOT NULL,
                                expires_at  TIMESTAMPTZ NOT NULL,
                                revoked     BOOLEAN NOT NULL DEFAULT FALSE,
                                created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
                                updated_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_refresh_tokens_family_id ON refresh_tokens(family_id);