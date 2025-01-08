document.addEventListener("DOMContentLoaded", () => {

    const reviewsContainer = document.getElementById("review-container");
    let nextBtn = document.getElementById('next-btn');
    let limit = 5;

    function loadReview(limit) {
        fetch(`/api/users/reviews?limit=${limit}`)
            .then(response => response.json())
            .then(data => {
                if (data.reviews.length > 0) {
                    data.reviews.forEach((review, index) => {

                        const card = document.createElement("div");
                        card.classList.add("card");

                        const cardContent = document.createElement("div");
                        cardContent.classList.add("card-content");

                        //상품명, 이름, 평점내용 추가
                        addCardContent(cardContent, review)

                        //이미지가 있을 시 이미지 테그 생성 후 추가
                        createReviewImg(review, index, cardContent);


                        let description = document.createElement('p');
                        description.textContent = review.description

                        cardContent.appendChild(description);
                        card.appendChild(cardContent);
                        reviewsContainer.appendChild(card);
                    });

                    createNextBtn(data.hasNext);
                } else {
                    reviewsContainer.innerHTML = "<h4>작성하신 리뷰가 없습니다.</h4>";
                }
            })
            .catch(error => console.error("Error fetching reviews:", error));
    }

    loadReview(limit);

    function nextLoadReview() {
        limit += 5;
        loadReview(limit);
    }

    function addCardContent(cardContent, review) {
        cardContent.innerHTML = `
                                <h4>${review.itemName}</h4>
                                <span class="stars">★</span>
                                <span>${review.rate}점 | </span>
                                <span>${review.createAt}</span>
                                <button class="cancel-btn">삭제</button>
                            `;

        const deleteBtn = cardContent.querySelector(".cancel-btn");
        deleteBtn.addEventListener('click', () => {
            deleteReview(review.reviewId);
        })
    }

    function createReviewImg(review, index, cardContent) {
        if (review.reviewImgPath.length > 0) {
            // Carousel 생성
            const carouselId = `carousel-${index}`;
            const carousel = document.createElement("div");
            carousel.id = carouselId;
            carousel.classList.add("carousel", "slide");
            carousel.setAttribute("data-bs-interval", "false");

            const indicators = document.createElement("div");
            indicators.classList.add("carousel-indicators");

            const inner = document.createElement("div");
            inner.classList.add("carousel-inner");

            review.reviewImgPath.forEach((image, imgIndex) => {

                const indicator = document.createElement("button");
                indicator.type = "button";
                indicator.setAttribute("data-bs-target", `#${carouselId}`);
                indicator.setAttribute("data-bs-slide-to", imgIndex);
                indicator.setAttribute("aria-label", `Slide ${imgIndex + 1}`);
                if (imgIndex === 0) {
                    indicator.classList.add("active");
                    indicator.setAttribute("aria-current", "true");
                }
                indicators.appendChild(indicator);

                // Carousel item 추가
                const item = document.createElement("div");
                item.classList.add("carousel-item");
                if (imgIndex === 0) item.classList.add("active");

                const img = document.createElement("img");
                img.src = image;
                img.classList.add("d-block", "w-100");
                img.alt = `Image ${imgIndex + 1}`;
                item.appendChild(img);

                inner.appendChild(item);
            });

            // Carousel navigation 추가
            const prevButton = `
                                    <button class="carousel-control-prev" type="button" data-bs-target="#${carouselId}" data-bs-slide="prev">
                                        <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                                        <span class="visually-hidden">Previous</span>
                                    </button>`;
            const nextButton = `
                                    <button class="carousel-control-next" type="button" data-bs-target="#${carouselId}" data-bs-slide="next">
                                        <span class="carousel-control-next-icon" aria-hidden="true"></span>
                                        <span class="visually-hidden">Next</span>
                                    </button>`;

            carousel.appendChild(indicators);
            carousel.appendChild(inner);
            carousel.innerHTML += prevButton + nextButton;
            cardContent.appendChild(carousel);
        }
    }

    function createNextBtn(hasNext) {
        if (hasNext) {
            if (nextBtn) {
                reviewsContainer.removeChild(nextBtn);
            } else {
                nextBtn = document.createElement('button');
                nextBtn.id = 'next-btn';
                nextBtn.classList.add('next-btn');
                nextBtn.textContent = "다음 리뷰";
                nextBtn.addEventListener('click', () => nextLoadReview());
                reviewsContainer.appendChild(nextBtn);
            }
        }
    }

    function deleteReview(reviewId) {


        const url = `/api/users/reviews/${reviewId}`;

        if (confirm("정말로 삭제하시겠습니까?")) {
            fetch(url, {
                method : 'DELETE'
            })
                .then(response => {
                    if (response.ok) {
                        alert("정상적으로 삭제가 되었습니다.");
                        window.location.href = '/users/reviews';
                    } else {
                        alert("정상적으로 삭제가 되지 않았습니다.");
                    }
                });
        }
    }
});