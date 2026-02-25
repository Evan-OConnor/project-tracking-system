document.addEventListener("DOMContentLoaded", function () {

    const deleteBtn = document.getElementById("deleteBtn");
    if (!deleteBtn) return;

    deleteBtn.addEventListener("click", function () {
        const id = this.dataset.id;

        if (!confirm("Delete this timesheet entry?")) return;

        fetch(`/timesheet-entries/${id}/delete`, { method: "POST" })
            .then(response => {
                if (response.ok) {
                    showToast("Timesheet entry deleted successfully");
                    // Redirect to list after short delay
                    setTimeout(() => {
                        window.location.href = "/timesheet-entries";
                    }, 1000);
                } else {
                    showToast("Delete failed", true);
                }
            })
            .catch(() => showToast("Server error occurred", true));
    });

    function showToast(message, isError = false) {
        const toastEl = document.getElementById("appToast");
        const toastMessage = document.getElementById("toastMessage");
        if (!toastEl || !toastMessage) return;

        toastMessage.textContent = message;
        toastEl.classList.remove("bg-success","bg-danger");
        toastEl.classList.add(isError ? "bg-danger" : "bg-success");
        new bootstrap.Toast(toastEl).show();
    }

});