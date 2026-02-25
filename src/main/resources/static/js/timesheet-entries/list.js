document.addEventListener("DOMContentLoaded", function () {

    const searchInput = document.getElementById("searchInput"); // optional search box
    const tableRows = document.querySelectorAll("#entriesTable tbody tr");

    // Live search
    if (searchInput) {
        searchInput.addEventListener("keyup", function () {
            const filter = this.value.toLowerCase();
            tableRows.forEach(row => {
                const projectCell = row.cells[0];
                const employeeCell = row.cells[1];
                if (!projectCell || !employeeCell) return;

                const text = projectCell.textContent.toLowerCase() + " " + employeeCell.textContent.toLowerCase();
                row.style.display = text.includes(filter) ? "" : "none";
            });
        });
    }

    // AJAX Delete
    document.querySelectorAll(".delete-btn").forEach(button => {
        button.addEventListener("click", function () {
            const id = this.dataset.id;
            if (!confirm("Delete this timesheet entry?")) return;

            fetch(`/timesheet-entries/${id}/delete`, { method: "POST" })
                .then(response => {
                    if (response.ok) {
                        this.closest("tr").remove();
                        showToast("Timesheet entry deleted successfully");
                    } else {
                        showToast("Delete failed", true);
                    }
                })
                .catch(() => showToast("Server error occurred", true));
        });
    });

    // Toast helper
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