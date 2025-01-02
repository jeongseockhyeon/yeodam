document.querySelector("#guideEditForm").addEventListener("submit", function (event) {
    event.preventDefault();

    const form = event.target;
    const formData = new FormData(form);
    const jsonData = {};
    formData.forEach((value, key) => {
        jsonData[key] = value;
    });

    const guideId = document.getElementById('guideId').value;
    fetch(`/api/guides/edit/${guideId}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(jsonData),
    })
    .then(response => {
        if (response.ok) {
            alert('수정이 완료되었습니다!');
            window.location.href = "/sellers/guide-list";
        } else {
            return response.json().then(errorData => {
                alert(`수정 중 오류가 발생했습니다: ${errorData.message}`);
            });
        }
    })
    .catch(error => {
        console.error("Error:", error);
        alert("오류가 발생했습니다. 다시 시도해주세요.");
    });
});