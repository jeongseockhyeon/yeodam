document.addEventListener("DOMContentLoaded", () => {
    const dropdowns = document.querySelectorAll(".dropdown");

    dropdowns.forEach((dropdown) => {
        const button = dropdown.querySelector(".dropdown-btn");
        button.addEventListener("click", (event) => {
            // 현재 드롭다운이 이미 활성화되어 있으면 active 클래스를 제거
            const isActive = dropdown.classList.contains("active");


            // 현재 드롭다운 활성화/비활성화
            if (!isActive) {
                dropdown.classList.add("active");
            } else {
                dropdown.classList.remove("active");
            }

            // 클릭 이벤트가 부모로 전파되지 않도록 방지 (document의 클릭 이벤트를 방지)
            event.stopPropagation();
        });
    });
});