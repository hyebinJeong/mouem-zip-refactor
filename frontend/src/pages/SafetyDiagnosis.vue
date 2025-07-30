<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const selectedFile = ref(null);

function handleFileChange(event) {
  selectedFile.value = event.target.files[0];
}

async function uploadFile() {
  if (!selectedFile.value) {
    alert('PDF 파일을 선택해주세요.');
    return;
  }

  const formData = new FormData();
  formData.append('file', selectedFile.value);

  try {
    const response = await fetch('http://localhost:8080/safety-check', {
      method: 'POST',
      body: formData,
    });
    if (response.ok) {
      const registerId = await response.json();

      router.push(`/safety-check/${registerId}`);
    } else {
      console.error('분석 실패');
    }
  } catch (error) {
    console.error('업로드 중 오류 발생:', error);
  }
}
</script>

<template>
  <!-- 업로드 UI -->
  <div>
    <input type="file" @change="handleFileChange" accept="application/pdf" />
    <button
      @click="uploadFile"
      class="mt-2 bg-blue-500 text-white px-4 py-2 rounded"
    >
      다음
    </button>
  </div>
</template>
