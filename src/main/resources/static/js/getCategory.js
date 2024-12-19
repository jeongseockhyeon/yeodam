document.addEventListener("DOMContentLoaded", () => {
    const categoryCheckBox = document.getElementById("categoryCheckBox");

    //DB 내 카테고리
    fetch("/api/categories")
        .then(response => response.json())
        .then(categories => {
            categories.forEach(category => {
                const checkbox = document.createElement("input");
                checkbox.type = "checkbox";
                checkbox.value = category.id;
                checkbox.id = `category-${category.id}`;
                checkbox.name = "categories";

                const label = document.createElement("label");
                label.htmlFor = `category-${category.id}`;
                label.textContent = category.name;

                const div = document.createElement("div");
                div.appendChild(checkbox);
                div.appendChild(label);

                categoryCheckBox.appendChild(div);
            })
        })
        .catch(error => console.error("카테고리 불러오기 실패:", error));
});