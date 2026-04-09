document.addEventListener("DOMContentLoaded", function () {

// Live Search
    const searchInput = document.getElementById("searchInput");
    if (!searchInput) {
        return;
    }

    const tableRows = document.querySelectorAll("table tbody tr");

    searchInput.addEventListener("keyup", function () {

        const filter = this.value.toLowerCase();

        tableRows.forEach(row => {

            let match = false;

            row.querySelectorAll("td").forEach(td => {
                if (td.textContent.toLowerCase().includes(filter)) {
                    match = true;
                }
            });

            row.style.display = match ? "" : "none";

        });

    });

});

