// 삭제 버튼 이벤트 위임
const tableBody = document.querySelector("#tourTable tbody");
tableBody.addEventListener("click", (event) => {
    if (event.target.classList.contains("delete-button")) {
        const tourId = event.target.dataset.id; // 버튼의 data-id 값으로 ID 가져오기

        // 삭제 확인
        const confirmDelete = confirm("정말로 삭제하시겠습니까?");
        if (!confirmDelete) return;

        // 삭제 요청 보내기
        fetch(`/api/tours/${tourId}`, {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json",
            },
        })
            .then((response) => {
                if (response.ok) {
                    alert("삭제가 완료되었습니다.");
                    event.target.closest("tr").remove(); // 해당 행 삭제
                } else {
                    alert("삭제 중 문제가 발생했습니다.");
                }
            })
            .catch((error) => {
                console.error("Error during deletion:", error);
                alert("서버와의 통신 중 문제가 발생했습니다.");
            });
    }
});