document.addEventListener("DOMContentLoaded", function () {

    const deleteBtn = document.getElementById("deleteBtn");
    if (!deleteBtn) return;

    deleteBtn.addEventListener("click", function () {

        const id = this.dataset.id;

        if (!confirm("Are you sure you want to delete this employee?")) return;

        fetch(`/employees/${id}/delete`, { method: "POST" })
            .then(response => {
                if (response.ok) {
                    showToast("Employee deleted successfully");
                    // Redirect to list after a short delay
                    setTimeout(() => {
                        window.location.href = "/employees";
                    }, 1000);
                } else {
                    showToast("Delete failed", true);
                }
            })
            .catch(() => showToast("Server error occurred", true));

    });

    // Toast helper
    function showToast(message, isError = false) {
        const toastEl = document.getElementById("appToast");
        const toastMessage = document.getElementById("toastMessage");
        if (!toastEl || !toastMessage) return;

        toastMessage.textContent = message;
        toastEl.classList.remove("bg-success", "bg-danger");
        toastEl.classList.add(isError ? "bg-danger" : "bg-success");
        new bootstrap.Toast(toastEl).show();
    }

});