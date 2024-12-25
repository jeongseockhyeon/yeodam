document.addEventListener("DOMContentLoaded", () => {
    const dropdowns = document.querySelectorAll(".dropdown");

    dropdowns.forEach((dropdown) => {
        const button = dropdown.querySelector(".dropdown-btn");
        button.addEventListener("click", () => {
            // 다른 열려있는 드롭다운 닫기
            dropdowns.forEach((otherDropdown) => {
                if (otherDropdown !== dropdown) {
                    otherDropdown.classList.remove("active");
                }
            });

            // 현재 드롭다운 활성화/비활성화
            dropdown.classList.toggle("active");
        });
    });

    // 드롭다운 외부 클릭 시 닫기
    document.addEventListener("click", (event) => {
        if (!event.target.closest(".dropdown")) {
            dropdowns.forEach((dropdown) => dropdown.classList.remove("active"));
        }
    });
});