document.addEventListener("DOMContentLoaded", function () {

    const searchInput = document.getElementById("searchInput");

    // Live search
    if (searchInput) {
        searchInput.addEventListener("keyup", function () {
            const filter = this.value.trim().toLowerCase();
            const tableRows = document.querySelectorAll("#employeesTable tbody tr");

            tableRows.forEach(row => {
                const nameCell = row.cells[1]; // Name is second column (index 1)
                if (!nameCell) return;

                const nameText = (nameCell.textContent || "").toLowerCase();
                row.style.display = nameText.includes(filter) ? "" : "none";
            });
        });
    }

});