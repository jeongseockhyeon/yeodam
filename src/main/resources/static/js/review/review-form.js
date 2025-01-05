document.addEventListener('DOMContentLoaded', () => {
    const starRating = document.getElementById('starRating');
    const hiddenRating = document.getElementById('hiddenRating');
    const imageUpload = document.getElementById('imageUpload');
    const imagePreview = document.getElementById('imagePreview');
    let rating = 0;

    function createStars() {
        for (let i = 1; i <= 5; i++) {
            const star = document.createElement('div');
            star.classList.add('star');
            star.dataset.value = i;
            starRating.appendChild(star);
            star.addEventListener('click', (e) => {
                const value = parseFloat(e.target.dataset.value);
                const offsetX = e.offsetX;
                const starWidth = e.target.offsetWidth;
                if (offsetX < starWidth / 2) {
                    rating = value - 0.5;
                } else {
                    rating = value;
                }
                updateStars();
                hiddenRating.value = rating;
                console.log(rating)
            });
            star.addEventListener('mousemove', (e) => {
                const value = parseFloat(e.target.dataset.value);
                const offsetX = e.offsetX; //클릭한 별 x 좌표 값
                const starWidth = e.target.offsetWidth; //클릭한 별 넓이
                if (offsetX < starWidth / 2) {
                    rating = value - 0.5;
                } else {
                    rating = value;
                }
                updateStars();
                hiddenRating.value = rating;
            });
        }
    }

    function updateStars() {
        const stars = starRating.querySelectorAll('.star');
        stars.forEach(star => {
            const starValue = parseFloat(star.dataset.value);
            star.classList.remove('filled', 'half');
            if (rating >= starValue) {
                star.classList.add('filled');
            } else if (rating > starValue - 1) {
                star.classList.add('half');
            }
        });
    }

    createStars();

    imageUpload.addEventListener('change', (event) => {
        const files = event.target.files;

        if (files.length > 2) {
            alert(`최대 2개의 파일만 업로드할 수 있습니다.`);
            imageUpload.value = '';
            imagePreview.innerHTML = '';
            return;
        }

        imagePreview.innerHTML = '';
        for (const file of files) {
            const reader = new FileReader();
            reader.onload = (e) => {
                const img = document.createElement('img');
                img.src = e.target.result;
                imagePreview.appendChild(img);
            }
            reader.readAsDataURL(file);
        }
    });
});