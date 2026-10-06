CREATE TABLE audit_logs (
                            id         UUID PRIMARY KEY,
                            user_id    UUID REFERENCES users(id) ON DELETE SET NULL,
                            event_type VARCHAR(50) NOT NULL,
                            ip_address VARCHAR(45),
                            metadata   TEXT,
                            created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_event_type ON audit_logs(event_type);