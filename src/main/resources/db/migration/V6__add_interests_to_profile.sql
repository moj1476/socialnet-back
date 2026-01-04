ALTER TABLE profiles
    ADD COLUMN interests TEXT[];

CREATE INDEX idx_profiles_interests ON profiles USING GIN (interests);