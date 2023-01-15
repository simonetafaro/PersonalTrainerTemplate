import styled from 'styled-components';


export const Container = styled.div`
	display: inline-flex;
    width: 100%;
`

export const Column = styled.div`
    float: left;
    width: 50%;  
    display: flex;
    justify-content: space-around;
`;

export const TrainingType = styled.div`
    display: inline-grid;
  -webkit-box-pack: center;
  justify-content: center;
  -webkit-box-align: center;
  align-items: center;
  border-radius: 4px;
  width: 350px;
  height: 350px;
  cursor: pointer;
  font-weight: 500;
  color: rgb(60, 58, 70);
  margin-bottom: 24px;
  margin-right: 72px;
  background: white;
  transition: border-radius 70ms cubic-bezier(0, 0, 0.38, 0.9) 0s;
`;

