import ValidatorGenerator from './ValidatorGenerator';

describe('validator.inputMatrix', () => {
    test('metaModel is empty', () => {
        const validator = new ValidatorGenerator({});
        expect(validator.inputMatrix).toEqual({});
    });

    test('metaModel has class and class has input', () => {
        const validator = new ValidatorGenerator({
            classes: [
                {
                    name: 'Class',
                    inputs: [
                        {
                            type: 'Reference',
                            lowerBound: 2,
                            upperBound: 3,
                        }
                    ],
                }
            ],
        });
        expect(validator.inputMatrix).toEqual({
            'Class': {
                'Reference': {
                    'upperBound': 3,
                    'lowerBound': 2,
                }
            },
        });
    });
});

describe('validator.outputMatrix', () => {
    test('metaModel is empty', () => {
        const validator = new ValidatorGenerator({});
        expect(validator.outputMatrix).toEqual({});
    });

    test('metaModel has class and class has input', () => {
        const validator = new ValidatorGenerator({
            classes: [
                {
                    name: 'Class',
                    outputs: [
                        {
                            type: 'Reference',
                            lowerBound: 5,
                            upperBound: 7,
                        }
                    ],
                }
            ],
        });
        expect(validator.outputMatrix).toEqual({
            'Class': {
                'Reference': {
                    'upperBound': 7,
                    'lowerBound': 5,
                }
            },
        });
    });
});

describe('validator.getEdgeData', () => {
    test('diagram has no edges', () => {
        const validator = new ValidatorGenerator({}, {});
        expect(validator.getEdgeData('Edge')).not.toBeDefined();
    });

    test('diagram has edge', () => {
        const validator = new ValidatorGenerator({}, {
            model: {
                edges: [
                    {
                        name: 'Edge',
                        mReference: 'Reference',
                        from: 'SourceMClass',
                        to: 'TargetMClass',
                        connection: {
                            name: 'Connection'
                        }
                    }
                ]
            }
        });
        expect(validator.getEdgeData('Edge')).toEqual({
            'type': 'Reference',
            'from': 'SourceMClass',
            'to': 'TargetMClass',
            'style': 'Connection',
        });
    });
});