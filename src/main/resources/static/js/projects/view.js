document.addEventListener("DOMContentLoaded", function () {

    const deleteBtn = document.getElementById("deleteBtn");

    if (!deleteBtn) return;

    deleteBtn.addEventListener("click", function () {
        const id = this.dataset.id;
        if (!confirm("Delete this project?")) return;

        fetch(`/projects/${id}/delete`, { method: "POST" })
            .then(response => {
                if (response.ok) {
                    window.location.href = "/projects";
                } else {
                    showToast("Delete failed", true);
                }
            })
            .catch(() => showToast("Server error occurred", true));
    });

    console.log("JS is loaded!");
});