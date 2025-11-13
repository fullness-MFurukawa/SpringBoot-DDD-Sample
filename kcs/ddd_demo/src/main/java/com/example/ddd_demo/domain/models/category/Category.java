    package com.example.ddd_demo.domain.models.category;

    import com.example.ddd_demo.domain.exception.DomainException;
    /**
     * 商品カテゴリを表すエンティティ
     * - 同一性: CategoryId(値で等価)
     * - 属性: CategoryName(不変/自己検証済みのVO)
     */
    public final class Category {
        
        /**
         * 商品カテゴリId
         */
        private final CategoryId categoryId;
        /**
         * 商品カテゴリ名
         */
        private CategoryName name;

        /**
         * 生成: 新規作成
         * @param name
         * @return 商品カテゴリエンティティ
         */
        public static Category createNew(CategoryName name) {
            return new Category(CategoryId.createNew(), name);
        }

        /**
         * 生成: 識別子を指定して再構築(リストア)
         * - 既存データの復元やテストの明示的なID指定に利用する
         */
        public static Category restore(CategoryId id, CategoryName name) {
            return new Category(id, name);
        }

        /**
         * コンストラクタ
         * @param id 商品カテゴリId
         * @param name 商品カテゴリ名
         */
        private Category(CategoryId id, CategoryName name) {
            // CategoryIdがnullならドメインルール違反として例外をスロー
            if (id == null) {
                throw new DomainException("カテゴリIDは必須です。");
            }
            // CategoryNameがnullならドメインルール違反として例外をスロー
            if (name == null) {
                throw new DomainException("カテゴリ名は必須です。");
            }
            this.categoryId = id;
            this.name = name;   
        }

        /**
         * 名称を変更する
         * - nullは許可しない（ドメインルール違反としてDomainExceptionをスロー）
         * - 検証は CategoryName.of(...) に委譲されるため、
         *   不正な文字列や長さ超過もVO側でDomainExceptionとなる
         */
        public void rename(CategoryName newName) {
            if (newName == null) {
                throw new DomainException("カテゴリ名は必須です。");
            }
            this.name = newName;
        }

        /** 
         * ゲッター
         */
        public CategoryId getCategoryId() { return categoryId; }
        public CategoryName getName() { return name; }


        /**
         * 同一性による等価性 
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Category)) return false;
            Category other = (Category) o;
            return categoryId.equals(other.categoryId);
        }
        @Override
        public int hashCode() {
            return categoryId.hashCode();
        }

        /**
         * インスタンスの内容
         */
        @Override
        public String toString() {
            return "Category{id=" + categoryId + ", name=" + name + "}";
        }
    }
