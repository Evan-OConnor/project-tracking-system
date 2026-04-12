document.addEventListener('DOMContentLoaded', function () {

    const inputs = document.querySelectorAll('.client-search');

    inputs.forEach(input => {

        const resultsDiv = document.getElementById(input.dataset.resultsId);
        const hiddenIdInput = document.getElementById(input.dataset.targetId);

        if (!resultsDiv || !hiddenIdInput) return;

        let timeout;

        input.addEventListener('input', function () {
            const query = this.value;

            hiddenIdInput.value = ''; // clear selection

            clearTimeout(timeout);

            timeout = setTimeout(() => {

                if (query.length < 2) {
                    resultsDiv.innerHTML = '';
                    return;
                }

                // Fetch client
                fetch(`/api/clients/search?query=${encodeURIComponent(query)}`)
                    .then(res => res.json())
                    .then(data => {

                        resultsDiv.innerHTML = '';

                        if (data.length === 0) {
                            resultsDiv.innerHTML =
                                '<div class="list-group-item text-muted">No results found</div>';
                            return;
                        }

                        data.forEach(client => {

                            const item = document.createElement('a');
                            item.className = 'list-group-item list-group-item-action';
                            item.href = '#';
                            item.textContent = client.name;

                            item.addEventListener('click', function (e) {
                                e.preventDefault();

                                input.value = client.name;
                                hiddenIdInput.value = client.id;
                                resultsDiv.innerHTML = '';
                            });

                            resultsDiv.appendChild(item);
                        });
                    });

            }, 300);
        });

        // Optional: clear invalid input
        input.addEventListener('blur', function () {
            setTimeout(() => {
                if (!hiddenIdInput.value) {
                    input.value = '';
                }
            }, 150);
        });

    });

});