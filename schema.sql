-- User table
create table _user
(
    user_id   bigint       not null
        primary key,
    create_dt timestamp    not null,
    email     varchar(255) not null
        constraint uk_k11y3pdtsrjgy8w9b6q4bjwrx
            unique,
    password  varchar(255) not null,
    role      varchar(10)  not null,
    update_dt timestamp    not null
);

alter table address
    owner to "ved-asole";

-- Customer table
create table customer
(
    customer_id  bigint       not null
        primary key,
    create_dt    timestamp    not null,
    email        varchar(255) not null
        constraint uk_dwk6cx0afu8bs9o4t536v1j5v
            unique,
    first_name   varchar(20)  not null,
    last_name    varchar(20)  not null,
    phone_number varchar(255) not null,
    update_dt    timestamp    not null,
    address_id   bigint
        constraint "FKp0ork25utpkdwuhi874nf19cc"
            references address,
    user_id      bigint       not null
        constraint uk_j7ja2xvrxudhvssosd4nu1o92
            unique
        constraint "FKk2b4lf83ck1rq7vmfuver6e1c"
            references _user
);

alter table customer
    owner to "ved-asole";

-- Category Table
create table category
(
    category_id        bigint       not null
        primary key,
    active             boolean      not null,
    create_dt          timestamp    not null,
    "desc"             varchar(1000),
    image              varchar(255) not null,
    name               varchar(20)  not null,
    update_dt          timestamp    not null,
    parent_category_id bigint
        constraint "FK4wqwi3wgsrq5kka9k94vc5u2i"
            references category
);

alter table category
    owner to "ved-asole";

-- Product table
create table product
(
    product_id   bigint           not null
        primary key,
    create_dt    timestamp        not null,
    "desc"       varchar(1000),
    discount     double precision
        constraint product_discount_check
            check (discount >= (0)::double precision),
    image        varchar(255)     not null,
    name         varchar(255)     not null,
    price        double precision not null
        constraint product_price_check
            check (price >= (0)::double precision),
    qty_in_stock integer
        constraint product_qty_in_stock_check
            check (qty_in_stock >= 0),
    sku          varchar(255)     not null
        constraint uk_q1mafxn973ldq80m1irp3mpvq
            unique,
    update_dt    timestamp,
    category_id  bigint           not null
        constraint "FK7l29ekt1x29jup80y2iigimyy"
            references category
);

alter table product
    owner to "ved-asole";

create index product_name_idx
    on product (name);

create index product_desc_idx
    on product ("desc");

create index product_name_desc_idx
    on product (name, "desc");

create index product_category_idx
    on product (category_id);


-- Address table
alter table _user
    owner to "ved-asole";

create table address
(
    address_id  bigint       not null
        primary key,
    add_line1   varchar(100) not null,
    add_line2   varchar(100),
    city        varchar(50)  not null,
    country     varchar(50)  not null,
    create_dt   timestamp    not null,
    postal_code integer      not null,
    state       varchar(50)  not null,
    update_dt   timestamp    not null
);

--Cart Item table
create table cart_item
(
    cart_item_id bigint    not null
        primary key,
    create_dt    timestamp not null,
    quantity     bigint    not null
        constraint cart_item_quantity_check
            check (quantity >= 0),
    update_dt    timestamp,
    product_id   bigint    not null
        constraint "FKbqjyyaj7ikkmpvm4vw2l64y2s"
            references product,
    cart_id      bigint    not null
        constraint "FKlmddnw6pd7gder2x4r07f1ves"
            references cart
);

alter table cart_item
    owner to "ved-asole";

-- Cart table
create table cart
(
    cart_id     bigint           not null
        primary key,
    create_dt   timestamp        not null,
    discount    double precision
        constraint cart_discount_check
            check (discount >= (0)::double precision),
    total       double precision not null
        constraint cart_total_check
            check (total >= (0)::double precision),
    update_dt   timestamp,
    customer_id bigint           not null
        constraint uk_867x3yysb1f3jk41cv3vsoejj
            unique
        constraint "FKjkl19yyf10l5tb7j5npdhgy3b"
            references customer
);

alter table cart
    owner to "ved-asole";

-- Order Item table
create table order_item
(
    order_item_id bigint    not null
        primary key,
    create_dt     timestamp not null,
    quantity      bigint    not null
        constraint order_item_quantity_check
            check (quantity >= 0),
    update_dt     timestamp,
    order_id      bigint    not null
        constraint "FKl1bqqbilx1hdy29vykrqkgu3p"
            references "order",
    product_id    bigint    not null
        constraint "FKsxgfmcie6oo67uxtk9hqk02mq"
            references product
);

-- Order table
create table "order"
(
    order_id     bigint           not null
        primary key,
    create_dt    timestamp        not null,
    order_status varchar(30)      not null,
    total        double precision not null
        constraint order_total_check
            check (total >= (0)::double precision),
    update_dt    timestamp,
    address_id   bigint           not null
        constraint "FKjm6o0lh0tgj5m2tshjbaw5moj"
            references address,
    customer_id  bigint           not null
        constraint "FKk1m6gjs4m7rtgb5lw01g35yca"
            references customer
);

alter table "order"
    owner to "ved-asole";

create index order_customer_id_idx
    on "order" (customer_id);

create index order_customer_id_order_id_idx
    on "order" (customer_id, order_id);