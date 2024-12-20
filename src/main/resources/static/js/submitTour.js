document.addEventListener("DOMContentLoaded", () => {

    document.getElementById("submitBtn").addEventListener("click", () => {
        const selectedCategories = Array.from(
            document.querySelectorAll('input[name="categories"]:checked')
        ).map(checkbox => parseInt(checkbox.value));

        const formData = {
            sellerId: document.getElementById("sellerId").value,
            tourName: document.getElementById("tourName").value,
            tourDesc: document.getElementById("tourDesc").value,
            tourPeriod: document.getElementById("tourPeriod").value,
            tourRegion: document.getElementById("tourRegion").value,
            tourPrice: parseInt(document.getElementById("tourPrice").value),
            categoryIdList: selectedCategories
        };

        fetch("/api/tours", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(formData)
        })
            .then(response => response.json())
            .then(data => {
                alert("상품이 성공적으로 등록되었습니다!");
                console.log("Response:", data);

                window.location.href = "/item/manage"
            })
            .catch(error => {
                alert("등록 중 오류가 발생했습니다.");
                console.error("Error:", error);
            });
    });
});