export interface Invoice {
  id: number;
  buyer: string;
  issueDate: string;
  dueDate: string;
  item: string;
  comment: string;
  price: number;
}