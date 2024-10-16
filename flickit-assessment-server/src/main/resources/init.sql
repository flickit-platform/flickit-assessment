-- public.fau_space definition

-- Drop table

-- DROP TABLE public.fau_space;

CREATE SEQUENCE public.fau_space_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

ALTER SEQUENCE public.fau_space_id_seq OWNER TO flickit;
GRANT ALL ON SEQUENCE public.fau_space_id_seq TO flickit;

CREATE TABLE public.fau_space (
	id int8 GENERATED BY DEFAULT AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE) NOT NULL,
	code varchar(50) NOT NULL,
	title varchar(100) NOT NULL,
	creation_time timestamptz NOT NULL,
	last_modification_time timestamptz NOT NULL,
	owner_id uuid NOT NULL,
	created_by uuid NOT NULL,
	last_modified_by uuid NOT NULL,
    deleted bool DEFAULT false NOT NULL,
    deletion_time int8 DEFAULT 0 NOT NULL,
	CONSTRAINT pk_fau_space PRIMARY KEY (id),
	CONSTRAINT uq_fau_space_code UNIQUE (code)
);


CREATE INDEX account_space_code_cc3cf1de_like ON public.fau_space USING btree (code varchar_pattern_ops);
CREATE INDEX account_space_owner_id_9c7e98ca ON public.fau_space USING btree (owner_id);

-- public.fau_space_user_access definition

-- Drop table

-- DROP TABLE public.fau_space_user_access;

CREATE TABLE public.fau_space_user_access (
	space_id int8 NOT NULL,
	user_id uuid NOT NULL,
	created_by uuid NOT NULL,
	creation_time timestamptz DEFAULT now() NOT NULL,
	last_seen timestamptz NOT NULL,
	CONSTRAINT pk_fau_space_user_access PRIMARY KEY (space_id, user_id)
);
CREATE INDEX account_useraccess_space_id_c683395f ON public.fau_space_user_access USING btree (space_id);
CREATE INDEX account_useraccess_user_id_abb729d3 ON public.fau_space_user_access USING btree (user_id);

-- Permissions

ALTER TABLE public.fau_space_user_access OWNER TO flickit;
GRANT ALL ON TABLE public.fau_space_user_access TO flickit;
