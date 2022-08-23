interface IPage<ObjectType> {
  currentPage: number,
  results: ObjectType[] | undefined,
  totalPages: number,
  totalResults: number,
}

export default class Page<Model> implements IPage<Model> {
  currentPage: number;
  results: Model[];
  totalPages: number;
  totalResults: number;

  constructor(currentPage: number, totalPages: number, totalResults: number, results: Model[]) {
    this.currentPage = currentPage;
    this.totalPages = totalPages;
    this.totalResults = totalResults;
    this.results = results;
  }
} 
