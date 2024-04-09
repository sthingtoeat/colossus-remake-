<template>
    <div class="container content-field">
        <div class="card">
            <div class="card-body">
                <slot></slot>
            </div>
        </div>
    </div>
</template>

<script>
</script>

<style scoped>
div.content-field {
    margin-top: 20px;
}
div.card{
    background-color: rgb(0,0,0,50%);
}
</style>