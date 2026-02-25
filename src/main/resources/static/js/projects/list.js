document.addEventListener("DOMContentLoaded", function () {

    const searchInput = document.getElementById("searchInput");
    const tableRows = document.querySelectorAll("#projectsTable tbody tr");

    // Live Search
    if (searchInput) {
        searchInput.addEventListener("keyup", function () {
            const filter = this.value.toLowerCase();

            tableRows.forEach(row => {
                const titleCell = row.cells[1]; // assuming title is in second column
                if (!titleCell) return;

                const title = titleCell.textContent.toLowerCase();
                row.style.display = title.includes(filter) ? "" : "none";
            });
        });
    }

    // AJAX Delete
    document.querySelectorAll(".delete-btn").forEach(button => {

        button.addEventListener("click", function () {

            const id = this.dataset.id;

            if (!confirm("Delete this project?")) return;

            fetch(`/projects/${id}/delete`, {
                method: "POST"
            })
                .then(response => {
                    if (response.ok) {
                        this.closest("tr").remove();
                        showToast("Project deleted successfully");
                    } else {
                        showToast("Delete failed", true);
                    }
                })
                .catch(() => {
                    showToast("Server error occurred", true);
                });

        });

    });

});