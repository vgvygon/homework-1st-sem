create table tasks (
    id bigserial primary key,
    title varchar(255) not null,
    description text,
    completed boolean not null default false,
    priority varchar(32) not null,
    due_date date,
    tags varchar(1000),
    created_at timestamp not null,
    updated_at timestamp not null
);

create table task_attachments (
    id bigserial primary key,
    task_id bigint not null,
    file_name varchar(255) not null,
    file_path varchar(1000) not null,
    content_type varchar(255),
    file_size bigint not null,
    created_at timestamp not null,
    constraint fk_task_attachments_task foreign key (task_id) references tasks(id) on delete cascade
);

create index idx_tasks_completed_priority on tasks(completed, priority);
create index idx_tasks_due_date on tasks(due_date);
create index idx_task_attachments_task_id on task_attachments(task_id);
