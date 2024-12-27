document.addEventListener("DOMContentLoaded", () => {
    const guideSelect = document.getElementById("guideSelect");
    const selectedGuidesText = document.getElementById("selectedGuidesText");
    const tourImagesInput = document.getElementById("tourImages");
    const previewContainer = document.getElementById("previewContainer");

    let selectedGuides = [];
    let selectedGuideNames = [];
    let selectedImages = [];

    // 가이드 선택 이벤트
    guideSelect.addEventListener("change", () => {
        const selectedOptions = Array.from(guideSelect.selectedOptions);
        const newGuideIds = selectedOptions.map(option => parseInt(option.value));
        const newGuideNames = selectedOptions.map(option => option.textContent);

        newGuideIds.forEach((id, index) => {
            if (!selectedGuides.includes(id)) {
                selectedGuides.push(id);
                selectedGuideNames.push(newGuideNames[index]);
            }
        });

        updateSelectedGuidesText();
    });

    // 가이드 텍스트 업데이트 함수
    function updateSelectedGuidesText() {
        selectedGuidesText.innerHTML = ""; // 기존 내용을 초기화
        selectedGuides.forEach((id, index) => {
            const guideSpan = document.createElement("span");
            guideSpan.style.marginRight = "10px"; // 이름 간 간격 추가

            const guideName = document.createElement("span");
            guideName.textContent = selectedGuideNames[index];

            const removeButton = document.createElement("button");
            removeButton.textContent = "X";
            removeButton.style.marginLeft = "5px"; // 이름과 버튼 간 간격 추가
            removeButton.style.cursor = "pointer";

            // 제거 버튼 클릭 이벤트
            removeButton.addEventListener("click", () => {
                selectedGuides.splice(index, 1); // 가이드 ID 제거
                selectedGuideNames.splice(index, 1); // 가이드 이름 제거
                updateSelectedGuidesText(); // 텍스트 갱신
            });

            guideSpan.appendChild(guideName);
            guideSpan.appendChild(removeButton);
            selectedGuidesText.appendChild(guideSpan);
        });

        // 선택된 가이드가 없으면 기본 메시지 표시
        if (selectedGuides.length === 0) {
            selectedGuidesText.textContent = "선택된 가이드가 없습니다.";
        }
    }

    // 이미지 선택 이벤트
    tourImagesInput.addEventListener("change", () => {
        const files = Array.from(tourImagesInput.files);
        selectedImages = [...selectedImages, ...files].filter((file, index, self) =>
            index === self.findIndex(f => f.name === file.name)
        );
        updatePreview();
    });

    function updatePreview() {
        previewContainer.innerHTML = ""; // 기존 미리보기 초기화
        selectedImages.forEach((file, index) => {
            const imgContainer = document.createElement("div");
            imgContainer.style.position = "relative";
            imgContainer.style.display = "inline-block";
            imgContainer.style.margin = "5px";

            const img = document.createElement("img");
            const reader = new FileReader();

            reader.onload = () => {
                img.src = reader.result;
            };
            reader.readAsDataURL(file);

            const deleteButton = document.createElement("button");
            deleteButton.textContent = "삭제";
            deleteButton.className = "remove-image";
            deleteButton.style.position = "absolute";
            deleteButton.style.top = "5px";
            deleteButton.style.right = "5px";


            deleteButton.onclick = () => {
                // 업로드된 파일에서 제거
                selectedImages = selectedImages.filter(f => f !== file);
                imgContainer.remove();
            };

            imgContainer.appendChild(img);
            imgContainer.appendChild(deleteButton);
            previewContainer.appendChild(imgContainer);
        });
    }

    // FormData 생성 및 전송
    document.getElementById("submitBtn").addEventListener("click", () => {
        if (!validationFormData()) return;

        const selectedCategories = Array.from(
            document.querySelectorAll('input[name="categories"]:checked')
        ).map(checkbox => parseInt(checkbox.value));

        const formData = new FormData();
        formData.append("tourName", document.getElementById("tourName").value);
        formData.append("tourDesc", document.getElementById("tourDesc").value);
        formData.append("tourPeriod", document.getElementById("tourPeriod").value);
        formData.append("tourRegion", document.getElementById("tourRegion").value);
        formData.append("maximum", parseInt(document.getElementById("maximum").value));
        formData.append("tourPrice", parseInt(document.getElementById("tourPrice").value));
        formData.append("categoryIdList", JSON.stringify(selectedCategories));

        // 가이드 ID 배열 추가
        selectedGuides.forEach(guideId => {
            formData.append("guideIdList", guideId);
        });

        // 이미지 파일 배열 추가
        selectedImages.forEach(image => {
            formData.append("tourImages", image);
        });

        fetch("/api/tours", {
            method: "POST",
            body: formData,
        })
            .then(response => {
                if (!response.ok) throw new Error("HTTP error " + response.status);
                return response.json();
            })
            .then(data => {
                alert("상품이 성공적으로 등록되었습니다!");
                window.location.href = "/item/manage";
            })
            .catch(error => {
                alert("등록 중 오류가 발생했습니다.");
                console.error("Error:", error);
            });
    });
});