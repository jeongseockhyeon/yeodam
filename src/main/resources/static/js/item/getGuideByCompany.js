document.addEventListener("DOMContentLoaded", () => {
    // 가이드 데이터 가져오기
    fetch("/api/guides/list/filtering")
        .then(response => response.json())
        .then(guides => {
            guides.forEach(guide => {
                const option = document.createElement("option");
                option.value = guide.id;
                option.textContent = guide.name;
                guideSelect.appendChild(option);
            });
        })
        .catch(error => console.error("가이드 데이터를 가져오는 중 오류 발생:", error));
});
