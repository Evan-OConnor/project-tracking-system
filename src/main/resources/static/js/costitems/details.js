document.addEventListener("DOMContentLoaded", function () {

    const deleteBtn = document.getElementById("deleteBtn");

    function showToast(message, isError = false) {
        const toastEl = document.getElementById('appToast');
        const toastMessage = document.getElementById('toastMessage');
        toastMessage.textContent = message;
        toastEl.classList.toggle('bg-success', !isError);
        toastEl.classList.toggle('bg-danger', isError);
        const toast = new bootstrap.Toast(toastEl);
        toast.show();
    }

    if (deleteBtn) {
        deleteBtn.addEventListener("click", function () {
            const id = this.dataset.id;

            if (!confirm("Delete this cost item?")) return;

            fetch(`/cost-items/${id}/delete`, { method: "POST" })
                .then(response => {
                    if (response.ok) {
                        showToast("Cost item deleted successfully");
                        setTimeout(() => window.location.href = "/cost-items", 1000);
                    } else {
                        showToast("Delete failed", true);
                    }
                })
                .catch(() => showToast("Server error occurred", true));
        });
    }

});