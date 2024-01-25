drop table if exists wiki_page;

drop table if exists page_title_log;


create table wiki_page (
                           active bit,
                           namespace INTEGER,
                           last_modified_at datetime(6),
                           current_revision_id BINARY(16),
                           last_modified_by BINARY(16),
                           page_id BINARY(16) not null,
                           title varchar(255),
                           version_token varchar(255),
                           primary key (page_id)
);


alter table wiki_page
    add constraint idx__wiki_page__title__namespace unique (title, namespace);


create table page_title_log
(
    namespace         integer,
    created_at        datetime(6),
    page_title_log_id BINARY(16) not null,
    page_title        varchar(255),
    title_update_type enum ('CREATED','DELETED'),
    primary key (page_title_log_id)
);


create index idx__page_title_log__created_date
    on page_title_log (created_at);


