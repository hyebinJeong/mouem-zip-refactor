import { useRouter } from 'vue-router';

export function useNavigation() {
  const router = useRouter();

  const goToHome = () => router.push('/');
  const goToMyPage = () => router.push('/my');

  return { goToHome, goToMyPage };
}
