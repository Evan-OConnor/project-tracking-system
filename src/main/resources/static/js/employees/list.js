document.addEventListener("DOMContentLoaded", function () {

    const searchInput = document.getElementById("searchInput");
    const tableRows = document.querySelectorAll("#employeesTable tbody tr");

    // Live search by Name
    if (searchInput) {
        searchInput.addEventListener("keyup", function () {
            const filter = this.value.toLowerCase();
            tableRows.forEach(row => {
                const nameCell = row.cells[1]; // Name is second column
                if (!nameCell) return;
                row.style.display = nameCell.textContent.toLowerCase().includes(filter) ? "" : "none";
            });
        });
    }

    // AJAX Delete
    document.querySelectorAll(".delete-btn").forEach(button => {
        button.addEventListener("click", function () {
            const id = this.dataset.id;
            if (!confirm("Delete this employee?")) return;

            fetch(`/employees/${id}/delete`, { method: "POST" })
                .then(response => {
                    if (response.ok) {
                        this.closest("tr").remove();
                        showToast("Employee deleted successfully");
                    } else {
                        showToast("Delete failed", true);
                    }
                })
                .catch(() => showToast("Server error occurred", true));
        });
    });

    // Toast helper
    function showToast(message, isError = false) {
        let toastEl = document.getElementById("appToast");
        let toastMessage = document.getElementById("toastMessage");
        toastMessage.textContent = message;
        toastEl.classList.remove("bg-success", "bg-danger");
        toastEl.classList.add(isError ? "bg-danger" : "bg-success");
        new bootstrap.Toast(toastEl).show();
    }

});