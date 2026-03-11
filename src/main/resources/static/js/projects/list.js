document.addEventListener("DOMContentLoaded", function () {

    const searchInput = document.getElementById("searchInput");
    const tableRows = document.querySelectorAll("#projectsTable tbody tr");

    if (searchInput) {
        searchInput.addEventListener("keyup", function () {
            const filter = this.value.toLowerCase();

            tableRows.forEach(row => {
                const titleCell = row.cells[1];
                if (!titleCell) return;

                const title = titleCell.textContent.toLowerCase();
                row.style.display = title.includes(filter) ? "" : "none";
            });
        });
    }

});