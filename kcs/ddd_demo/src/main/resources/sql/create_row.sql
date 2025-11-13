-- 必要なら UUID 生成拡張を有効化
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- 1) カテゴリを投入（id は IDENTITY、自動採番）
INSERT INTO public.product_category (name) VALUES
  ('文房具'),
  ('雑貨'),
  ('パソコン周辺機器');

-- 2) 商品を投入（カテゴリは“名前”で解決して id にマッピング）
WITH v(uuid, name, price, catname) AS (
  VALUES
  -- 文房具
  ('ac413f22-0cf1-490a-9635-7e9ca810e544'::uuid,'水性ボールペン(黒)',120,'文房具'),
  ('8f81a72a-58ef-422b-b472-d982e8665292'::uuid,'水性ボールペン(赤)',120,'文房具'),
  ('d952b98c-a1ea-478d-8380-3b90fde872ea'::uuid,'水性ボールペン(青)',120,'文房具'),
  ('9959e553-c9da-4646-bd85-8663a3541583'::uuid,'油性ボールペン(黒)',100,'文房具'),
  ('79023e82-9197-40a5-b236-26487f404be4'::uuid,'油性ボールペン(赤)',100,'文房具'),
  ('7dfd0fd0-0893-4d20-83ef-6f70aab0ab76'::uuid,'油性ボールペン(青)',100,'文房具'),
  ('dc7243af-c2ce-4136-bd5d-c6b28ee0a20a'::uuid,'蛍光ペン(黄)',130,'文房具'),
  ('83fbc81d-2498-4da6-b8c2-54878d3b67ff'::uuid,'蛍光ペン(赤)',130,'文房具'),
  ('ee4b3752-3fbd-45fc-afb5-8f37c3f701c9'::uuid,'蛍光ペン(青)',130,'文房具'),
  ('35cb51a7-df79-4771-9939-7f32c19bca45'::uuid,'蛍光ペン(緑)',130,'文房具'),
  ('e4850253-f363-4e79-8110-7335e4af45be'::uuid,'鉛筆(黒)',100,'文房具'),
  ('5ca7dbdf-0010-44c5-a001-e4c13c4fe3a1'::uuid,'鉛筆(赤)',100,'文房具'),
  ('fbc43b9b-90a9-4712-925c-4d66a2a30372'::uuid,'色鉛筆(12色)',400,'文房具'),
  ('4b3db238-8ada-49b4-bb60-1a034914e528'::uuid,'色鉛筆(48色)',1300,'文房具'),

  -- 雑貨
  ('debdbd8c-5b48-4b1a-9697-98ba321ddd40'::uuid,'レザーネックレス',300,'雑貨'),
  ('367197c5-32bd-479a-9102-c601145464c4'::uuid,'ワンタッチ開閉傘',3000,'雑貨'),
  ('657578d2-8820-4490-a6ec-06d9c7cccd0f'::uuid,'金魚風呂敷',500,'雑貨'),
  ('8c107894-4ebc-445b-9603-c9e8e6524f9d'::uuid,'折畳トートバッグ',600,'雑貨'),
  ('2f8e074c-d0b1-441b-9dd4-6cf0ec570ce6'::uuid,'アイマスク',900,'雑貨'),
  ('2fb9fe48-3520-47ef-9e1a-338db7152884'::uuid,'防水スプレー',500,'雑貨'),
  ('f536311a-b9de-4873-a603-70953a2261be'::uuid,'キーホルダ',800,'雑貨'),

  -- パソコン周辺機器
  ('82014174-6785-4242-b307-a806fd1f8470'::uuid,'ワイヤレスマウス',900,'パソコン周辺機器'),
  ('ddd1e5ae-fb90-4a47-bb87-c91b305c7444'::uuid,'ワイヤレストラックボール',1300,'パソコン周辺機器'),
  ('aa5e07aa-06f9-4037-9755-e1de3c0ad4ac'::uuid,'有線光学式マウス',500,'パソコン周辺機器'),
  ('53cfa873-c86b-48bd-a68c-458d7bb5c844'::uuid,'光学式ゲーミングマウス',4800,'パソコン周辺機器'),
  ('376f7a75-cc99-4428-b35a-889bcb3c90af'::uuid,'有線ゲーミングマウス',3800,'パソコン周辺機器'),
  ('38c6e236-90ca-48a2-b427-acb9d834b591'::uuid,'USB有線式キーボード',1400,'パソコン周辺機器'),
  ('dc2e5a33-a2b7-4414-9a53-f9750e7da8ed'::uuid,'無線式キーボード',1900,'パソコン周辺機器')
)
INSERT INTO public.product (product_uuid, name, price, category_id)
SELECT v.uuid, v.name, v.price, c.id
FROM v
JOIN public.product_category c
  ON c.name = v.catname;

-- 3) すべての商品に在庫を一括付与（例: 初期在庫 100）
INSERT INTO public.product_stock (stock_uuid, stock, product_id)
SELECT gen_random_uuid(), 100, p.id
FROM public.product p;
