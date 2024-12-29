document.addEventListener("DOMContentLoaded", () => {
    // 모든 삭제 버튼 가져오기
    const deleteButtons = document.querySelectorAll(".delete-button");

    // 각 버튼에 클릭 이벤트 추가
    deleteButtons.forEach((button) => {
        button.addEventListener("click", () => {
            const tourId = button.value; // 버튼의 value 값으로 ID 가져오기

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
                        window.location.href = "/item/manage"; // 리다이렉트
                    } else {
                        alert("삭제 중 문제가 발생했습니다.");
                    }
                })
                .catch((error) => {
                    console.error("Error during deletion:", error);
                    alert("서버와의 통신 중 문제가 발생했습니다.");
                });
        });
    });
});