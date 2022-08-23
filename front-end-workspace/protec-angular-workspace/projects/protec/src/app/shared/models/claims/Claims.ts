export interface Claims {
    id: number,
    title: string,
    mainData: [
        {
            id: string,
            label: string,
            value: string
        }
    ]
}