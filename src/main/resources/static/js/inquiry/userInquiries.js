document.addEventListener("DOMContentLoaded", () => {
    const inquiryListContainer = document.getElementById("inquiry-list");
    const emptyMessage = document.getElementById("empty-message");

    // 문의 데이터를 가져오기
    const fetchInquiries = async () => {
        try {
            const response = await fetch("/inquiries/user/data", {
                method: "GET",
                headers: {
                    "Content-Type": "application/json"
                }
            });

            if (!response.ok) {
                throw new Error("Failed to fetch inquiries");
            }

            const inquiries = await response.json();

            if (inquiries.length === 0) {
                emptyMessage.style.display = "block";
                inquiryListContainer.style.display = "none";
            } else {
                emptyMessage.style.display = "none";
                inquiryListContainer.style.display = "block";
                renderInquiries(inquiries);
            }
        } catch (error) {
            console.error("Error fetching inquiries:", error);
            emptyMessage.style.display = "block";
            emptyMessage.textContent = "문의 내역을 가져오는 중 오류가 발생했습니다.";
        }
    };

    // HTML로 렌더링
    const renderInquiries = (inquiries) => {
        inquiryListContainer.innerHTML = "";

        inquiries.forEach((inquiry) => {
            const inquiryBox = document.createElement("div");
            inquiryBox.className = "inquiry-box";

            const itemName = document.createElement("div");
            itemName.className = "inquiry-itemName";
            itemName.textContent = inquiry.itemName;

            itemName.addEventListener("click", () => {
                window.location.href = `/tours/${inquiry.itemId}`;
            });

            const title = document.createElement("div");
            title.className = "inquiry-title";
            title.textContent = inquiry.title;

            const content = document.createElement("div");
            content.className = "inquiry-content";
            content.textContent = inquiry.content;

            const answerStatus = document.createElement("div");
            answerStatus.className = "answer-status";
            answerStatus.textContent = inquiry.isAnswered === "Y" ? "답변 완료" : "답변 대기";
            if (inquiry.isAnswered === "N") {
                answerStatus.classList.add("pending");
            }

            // 삭제 버튼 추가
            const deleteButton = document.createElement("button");
            deleteButton.className = "delete-button";
            deleteButton.textContent = "삭제";
            deleteButton.addEventListener("click", async () => {
                if (confirm("이 문의를 삭제하시겠습니까?")) {
                    try {
                        const response = await fetch(`/inquiries/${inquiry.id}`, {
                            method: "DELETE",
                        });
                        if (!response.ok) {
                            throw new Error("삭제 실패");
                        }
                        alert("문의가 삭제되었습니다.");
                        fetchInquiries();
                    } catch (error) {
                        console.error("Error deleting inquiry:", error);
                        alert("삭제 중 오류가 발생했습니다.");
                    }
                }
            });

            inquiryBox.appendChild(itemName);
            inquiryBox.appendChild(title);
            inquiryBox.appendChild(content);
            inquiryBox.appendChild(answerStatus);
            inquiryBox.appendChild(deleteButton);

            inquiryListContainer.appendChild(inquiryBox);
        });
    };

    // 초기 로드 시 API 호출
    fetchInquiries();
});