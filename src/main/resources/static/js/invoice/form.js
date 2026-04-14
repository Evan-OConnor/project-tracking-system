document.addEventListener("DOMContentLoaded", function () {

    const projectInput = document.getElementById("projectInput");
    const projectIdInput = document.getElementById("projectId");
    const suggestions = document.getElementById("projectSuggestions");

    if (!projectInput || !suggestions) return;

    let debounceTimer;

    projectInput.addEventListener("input", function () {

        const query = this.value;

        // reset ID when typing
        projectIdInput.value = "";

        clearTimeout(debounceTimer);

        debounceTimer = setTimeout(() => {

            if (query.length < 2) {
                suggestions.innerHTML = "";
                return;
            }

        // Fetch Project
            fetch("/api/projects/search?query=" + encodeURIComponent(query))
                .then(res => res.json())
                .then(data => {

                    suggestions.innerHTML = "";

                    data.forEach(item => {

                        const li = document.createElement("li");
                        li.className = "list-group-item list-group-item-action";
                        li.style.cursor = "pointer";

                        li.textContent = item.title;

                        li.onclick = () => {
                            projectInput.value = item.title;
                            projectIdInput.value = item.id;
                            suggestions.innerHTML = "";
                        };

                        suggestions.appendChild(li);
                    });
                });

        }, 300);
    });

});