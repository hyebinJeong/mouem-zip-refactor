export const gradeColor = {
  안전: '#00AEEF',
  보통: '#39B54A',
  주의: '#F7941D',
  위험: '#ED1C24',
  판단보류: '#6C757D',
};

// JSDoc으로 어떤 값인지 설명
/**
 * @param {string} grade - 등급명 ('안전', '보통', '주의' 등)
 * @returns {string} HEX 색상 문자열
 */

export function getGradeColor(grade) {
  return gradeColor[grade] || '#ddd';
}
